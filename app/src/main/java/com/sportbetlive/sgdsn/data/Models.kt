package com.sportbetlive.sgdsn.data

data class Sport(
    val id: Int,
    val name: String,
    val leagueName: String,
    val iconRes: Int,
    val sportType: SportType
)

enum class SportType {
    WORLD_FOOTBALL,
    EURO_FOOTBALL,
    BASKETBALL,
    HOCKEY,
    TENNIS
}

data class Team(
    val id: Int,
    val name: String,
    val shortName: String,
    val logoRes: Int,
    val founded: Int,
    val arena: String,
    val sportType: SportType
)

data class Standing(
    val position: Int,
    val team: Team,
    val played: Int,
    val wins: Int,
    val draws: Int,
    val losses: Int,
    val points: Int
)

data class MatchEvent(
    val minute: Int,
    val description: String,
    val isHome: Boolean
)

data class PeriodScore(
    val periodName: String,
    val homeScore: Int,
    val awayScore: Int,
    val events: List<MatchEvent>
)

data class MatchRecord(
    val homeTeam: Team,
    val awayTeam: Team,
    val homeScore: Int,
    val awayScore: Int
)

data class MatchResult(
    val homeTeam: Team,
    val awayTeam: Team,
    val periods: List<PeriodScore>
) {
    val homeScore: Int get() = periods.sumOf { it.homeScore }
    val awayScore: Int get() = periods.sumOf { it.awayScore }
    val events: List<MatchEvent> get() = periods.flatMap { it.events }
}
