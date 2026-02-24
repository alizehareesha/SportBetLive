package com.sportbetlive.sgdsn.util

import android.content.Context
import android.content.SharedPreferences
import com.sportbetlive.sgdsn.data.DataProvider
import com.sportbetlive.sgdsn.data.MatchRecord
import com.sportbetlive.sgdsn.data.SportType
import com.sportbetlive.sgdsn.data.Standing
import org.json.JSONArray
import org.json.JSONObject

class DataVault(context: Context) {

    private val prefs: SharedPreferences =
        context.getSharedPreferences("sport_win_vault", Context.MODE_PRIVATE)

    fun saveAccessKey(key: String) {
        prefs.edit().putString(KEY_ACCESS, key).apply()
    }

    fun getAccessKey(): String? = prefs.getString(KEY_ACCESS, null)

    fun saveContentLink(link: String) {
        prefs.edit().putString(KEY_CONTENT_LINK, link).apply()
    }

    fun getContentLink(): String? = prefs.getString(KEY_CONTENT_LINK, null)

    fun savePolicyLink(link: String) {
        prefs.edit().putString(KEY_POLICY_LINK, link).apply()
    }

    fun getPolicyLink(): String? = prefs.getString(KEY_POLICY_LINK, null)

    fun saveStandings(sportType: SportType, standings: List<Standing>) {
        val arr = JSONArray()
        for (s in standings) {
            val obj = JSONObject()
            obj.put("tid", s.team.id)
            obj.put("p", s.played)
            obj.put("w", s.wins)
            obj.put("d", s.draws)
            obj.put("l", s.losses)
            obj.put("pts", s.points)
            arr.put(obj)
        }
        prefs.edit().putString("standings_${sportType.name}", arr.toString()).apply()
    }

    fun getStandings(sportType: SportType): List<Standing>? {
        val raw = prefs.getString("standings_${sportType.name}", null) ?: return null
        return try {
            val arr = JSONArray(raw)
            val teams = DataProvider.getTeams(sportType)
            val list = mutableListOf<Standing>()
            for (i in 0 until arr.length()) {
                val obj = arr.getJSONObject(i)
                val tid = obj.getInt("tid")
                val team = teams.find { it.id == tid } ?: continue
                list.add(
                    Standing(
                        position = 0,
                        team = team,
                        played = obj.getInt("p"),
                        wins = obj.getInt("w"),
                        draws = obj.getInt("d"),
                        losses = obj.getInt("l"),
                        points = obj.getInt("pts")
                    )
                )
            }
            list.sortedByDescending { it.points }
                .mapIndexed { index, s -> s.copy(position = index + 1) }
        } catch (_: Exception) {
            null
        }
    }

    fun updateStandingsAfterMatch(
        sportType: SportType,
        homeTeamId: Int,
        awayTeamId: Int,
        homeScore: Int,
        awayScore: Int
    ) {
        val current = getStandings(sportType) ?: DataProvider.emptyStandings(sportType)
        val updated = current.map { standing ->
            when (standing.team.id) {
                homeTeamId -> {
                    val w = if (homeScore > awayScore) 1 else 0
                    val d = if (homeScore == awayScore) 1 else 0
                    val l = if (homeScore < awayScore) 1 else 0
                    val ptsAdd = when (sportType) {
                        SportType.BASKETBALL, SportType.TENNIS -> w
                        else -> w * 3 + d
                    }
                    standing.copy(
                        played = standing.played + 1,
                        wins = standing.wins + w,
                        draws = standing.draws + d,
                        losses = standing.losses + l,
                        points = standing.points + ptsAdd
                    )
                }
                awayTeamId -> {
                    val w = if (awayScore > homeScore) 1 else 0
                    val d = if (awayScore == homeScore) 1 else 0
                    val l = if (awayScore < homeScore) 1 else 0
                    val ptsAdd = when (sportType) {
                        SportType.BASKETBALL, SportType.TENNIS -> w
                        else -> w * 3 + d
                    }
                    standing.copy(
                        played = standing.played + 1,
                        wins = standing.wins + w,
                        draws = standing.draws + d,
                        losses = standing.losses + l,
                        points = standing.points + ptsAdd
                    )
                }
                else -> standing
            }
        }.sortedByDescending { it.points }
            .mapIndexed { index, s -> s.copy(position = index + 1) }

        saveStandings(sportType, updated)
    }

    fun saveMatchRecord(
        sportType: SportType,
        homeTeamId: Int,
        awayTeamId: Int,
        homeScore: Int,
        awayScore: Int
    ) {
        val key = "match_history_${sportType.name}"
        val raw = prefs.getString(key, null)
        val arr = if (raw != null) JSONArray(raw) else JSONArray()
        val obj = JSONObject()
        obj.put("hid", homeTeamId)
        obj.put("aid", awayTeamId)
        obj.put("hs", homeScore)
        obj.put("as", awayScore)
        arr.put(obj)
        prefs.edit().putString(key, arr.toString()).apply()
    }

    fun getMatchHistory(sportType: SportType, teamId: Int): List<MatchRecord> {
        val key = "match_history_${sportType.name}"
        val raw = prefs.getString(key, null) ?: return emptyList()
        val teams = DataProvider.getTeams(sportType)
        val result = mutableListOf<MatchRecord>()
        try {
            val arr = JSONArray(raw)
            for (i in 0 until arr.length()) {
                val obj = arr.getJSONObject(i)
                val hid = obj.getInt("hid")
                val aid = obj.getInt("aid")
                if (hid != teamId && aid != teamId) continue
                val home = teams.find { it.id == hid } ?: continue
                val away = teams.find { it.id == aid } ?: continue
                result.add(
                    MatchRecord(
                        homeTeam = home,
                        awayTeam = away,
                        homeScore = obj.getInt("hs"),
                        awayScore = obj.getInt("as")
                    )
                )
            }
        } catch (_: Exception) { }
        return result.takeLast(10).reversed()
    }

    fun clearSimulationData() {
        val editor = prefs.edit()
        for (sport in SportType.entries) {
            editor.remove("standings_${sport.name}")
            editor.remove("match_history_${sport.name}")
        }
        editor.apply()
    }

    fun clearAll() {
        prefs.edit().clear().apply()
    }

    companion object {
        private const val KEY_ACCESS = "access_key"
        private const val KEY_CONTENT_LINK = "content_link"
        private const val KEY_POLICY_LINK = "policy_link"
    }
}
