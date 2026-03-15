package com.sportbetlive.sgdsn.data

import com.sportbetlive.sgdsn.R
import kotlin.random.Random

object DataProvider {

    fun getSports(): List<Sport> = listOf(
        Sport(0, "World Football", "Global Champions Cup", R.drawable.ic_sport_football, SportType.WORLD_FOOTBALL),
        Sport(1, "European Football", "Continental Premier League", R.drawable.ic_sport_euro_football, SportType.EURO_FOOTBALL),
        Sport(2, "Basketball", "Supreme Basketball Association", R.drawable.ic_sport_basketball, SportType.BASKETBALL),
        Sport(3, "Hockey", "Ice Legends League", R.drawable.ic_sport_hockey, SportType.HOCKEY),
        Sport(4, "Tennis", "Grand Slam Masters Tour", R.drawable.ic_sport_tennis, SportType.TENNIS)
    )

    fun getTeams(sportType: SportType): List<Team> = when (sportType) {
        SportType.WORLD_FOOTBALL -> listOf(
            Team(100, "Nordland FC", "NOR", R.drawable.logo_nordland, 1921, "Frostheim Arena", sportType),
            Team(101, "Azurea United", "AZU", R.drawable.logo_azurea, 1935, "Blue Wave Stadium", sportType),
            Team(102, "Stormvale City", "STV", R.drawable.logo_stormvale, 1948, "Thunderdome Park", sportType),
            Team(103, "Solaris SC", "SOL", R.drawable.logo_solaris, 1912, "Sunfire Arena", sportType),
            Team(104, "Ironpeak Rangers", "IPR", R.drawable.logo_ironpeak, 1957, "Steel Mountain Ground", sportType),
            Team(105, "Oceana FC", "OCE", R.drawable.logo_oceana, 1963, "Tidewater Stadium", sportType),
            Team(106, "Terranova United", "TER", R.drawable.logo_terranova, 1940, "New Earth Park", sportType),
            Team(107, "Skyridge Rovers", "SKY", R.drawable.logo_skyridge, 1929, "Cloud Summit Arena", sportType)
        )
        SportType.EURO_FOOTBALL -> listOf(
            Team(200, "Royal Lions", "RLI", R.drawable.logo_royal_lions, 1899, "Crown Stadium", sportType),
            Team(201, "Crimson Eagles", "CEA", R.drawable.logo_crimson_eagles, 1910, "Redwing Arena", sportType),
            Team(202, "Blue Titans", "BTI", R.drawable.logo_blue_titans, 1925, "Colosseum Park", sportType),
            Team(203, "Silver Wolves", "SWO", R.drawable.logo_silver_wolves, 1933, "Moonlight Ground", sportType),
            Team(204, "Golden Knights", "GKN", R.drawable.logo_golden_knights, 1902, "Valor Stadium", sportType),
            Team(205, "Phoenix Rising", "PHR", R.drawable.logo_phoenix_rising, 1947, "Ember Field", sportType),
            Team(206, "Storm United", "STU", R.drawable.logo_storm_united, 1918, "Gale Force Arena", sportType),
            Team(207, "Eclipse FC", "ECL", R.drawable.logo_eclipse, 1955, "Shadow Park", sportType)
        )
        SportType.BASKETBALL -> listOf(
            Team(300, "Thunder Bears", "TBR", R.drawable.logo_thunder_bears, 1965, "Roar Dome", sportType),
            Team(301, "Steel Hawks", "SHK", R.drawable.logo_steel_hawks, 1972, "Iron Wing Center", sportType),
            Team(302, "Flame Stallions", "FST", R.drawable.logo_flame_stallions, 1968, "Blaze Arena", sportType),
            Team(303, "Ice Cobras", "ICO", R.drawable.logo_ice_cobras, 1980, "Venom Court", sportType),
            Team(304, "Night Raptors", "NRA", R.drawable.logo_night_raptors, 1975, "Dusk Center", sportType),
            Team(305, "Volt Strikers", "VST", R.drawable.logo_volt_strikers, 1988, "Spark Arena", sportType),
            Team(306, "Shadow Panthers", "SPA", R.drawable.logo_shadow_panthers, 1970, "Dark Court", sportType),
            Team(307, "Stone Gorillas", "SGO", R.drawable.logo_stone_gorillas, 1983, "Boulder Dome", sportType)
        )
        SportType.HOCKEY -> listOf(
            Team(400, "Frost Wolves", "FWO", R.drawable.logo_frost_wolves, 1950, "Glacier Rink", sportType),
            Team(401, "Blizzard Kings", "BKI", R.drawable.logo_blizzard_kings, 1945, "Snowstorm Palace", sportType),
            Team(402, "Glacier Sharks", "GSH", R.drawable.logo_glacier_sharks, 1960, "Deep Freeze Arena", sportType),
            Team(403, "Avalanche Force", "AVF", R.drawable.logo_avalanche_force, 1958, "Sliderock Rink", sportType),
            Team(404, "Polar Titans", "PTI", R.drawable.logo_polar_titans, 1953, "North Star Center", sportType),
            Team(405, "Arctic Falcons", "AFA", R.drawable.logo_arctic_falcons, 1967, "Tundra Arena", sportType),
            Team(406, "Snowstorm Vipers", "SVP", R.drawable.logo_snowstorm_vipers, 1962, "Hailstone Rink", sportType),
            Team(407, "Permafrost Knights", "PFK", R.drawable.logo_permafrost_knights, 1970, "Iceguard Fortress", sportType)
        )
        SportType.TENNIS -> listOf(
            Team(500, "Max Sterling", "MST", R.drawable.logo_sterling, 1995, "Sterling Academy", sportType),
            Team(501, "Leo Vance", "LVA", R.drawable.logo_vance, 1998, "Vance Tennis Club", sportType),
            Team(502, "Adrian Cross", "ACR", R.drawable.logo_cross, 1993, "Crossway Courts", sportType),
            Team(503, "Kai Novak", "KNO", R.drawable.logo_novak, 1997, "Novak Arena", sportType),
            Team(504, "Ethan Rowe", "ERO", R.drawable.logo_rowe, 1996, "Rowe Tennis Center", sportType),
            Team(505, "Dominic Hale", "DHA", R.drawable.logo_hale, 1994, "Hale Courts", sportType),
            Team(506, "Viktor Maren", "VMA", R.drawable.logo_maren, 1992, "Maren Stadium", sportType),
            Team(507, "Julian Frost", "JFR", R.drawable.logo_frost, 1999, "Frost Courts", sportType)
        )
    }

    fun emptyStandings(sportType: SportType): List<Standing> {
        return getTeams(sportType).mapIndexed { index, team ->
            Standing(index + 1, team, 0, 0, 0, 0, 0)
        }
    }

    fun getPeriodNames(sportType: SportType): List<String> = when (sportType) {
        SportType.WORLD_FOOTBALL, SportType.EURO_FOOTBALL -> listOf("1st Half", "2nd Half")
        SportType.BASKETBALL -> listOf("Q1", "Q2", "Q3", "Q4")
        SportType.HOCKEY -> listOf("1st Period", "2nd Period", "3rd Period")
        SportType.TENNIS -> listOf("Set 1", "Set 2", "Set 3")
    }

    fun simulateMatch(home: Team, away: Team, sportType: SportType): MatchResult {
        val periodNames = getPeriodNames(sportType)
        val periods = mutableListOf<PeriodScore>()

        for ((periodIdx, periodName) in periodNames.withIndex()) {
            val maxPerPeriod = when (sportType) {
                SportType.BASKETBALL -> 35
                SportType.HOCKEY -> 3
                SportType.TENNIS -> 1
                else -> 3
            }
            val minPerPeriod = when (sportType) {
                SportType.BASKETBALL -> 18
                else -> 0
            }
            val homeP = Random.nextInt(minPerPeriod, maxPerPeriod + 1)
            val awayP = Random.nextInt(minPerPeriod, maxPerPeriod + 1)

            val periodEvents = mutableListOf<MatchEvent>()
            val minuteBase = when (sportType) {
                SportType.BASKETBALL -> periodIdx * 12
                SportType.HOCKEY -> periodIdx * 20
                SportType.TENNIS -> periodIdx * 60
                else -> periodIdx * 45
            }
            val minuteRange = when (sportType) {
                SportType.BASKETBALL -> 12
                SportType.HOCKEY -> 20
                SportType.TENNIS -> 60
                else -> 45
            }

            val eventNames = when (sportType) {
                SportType.BASKETBALL -> listOf("3-Point Shot", "Dunk", "Free Throw", "Layup", "Steal & Score")
                SportType.HOCKEY -> listOf("Goal", "Power Play Goal", "Shorthand Goal", "Penalty Shot Goal")
                SportType.TENNIS -> listOf("Ace", "Winner", "Break Point", "Set Won", "Double Fault")
                else -> listOf("Goal", "Free Kick Goal", "Penalty", "Header", "Long Range Shot")
            }

            val eventCount = when (sportType) {
                SportType.BASKETBALL -> Random.nextInt(2, 5)
                SportType.TENNIS -> Random.nextInt(2, 4)
                else -> (homeP + awayP).coerceAtLeast(1)
            }

            repeat(eventCount) {
                val isHome = Random.nextBoolean()
                periodEvents.add(
                    MatchEvent(
                        minute = minuteBase + Random.nextInt(1, minuteRange + 1),
                        description = "${eventNames.random()} by ${if (isHome) home.shortName else away.shortName}",
                        isHome = isHome
                    )
                )
            }

            periods.add(
                PeriodScore(
                    periodName = periodName,
                    homeScore = homeP,
                    awayScore = awayP,
                    events = periodEvents.sortedBy { it.minute }
                )
            )
        }

        return MatchResult(
            homeTeam = home,
            awayTeam = away,
            periods = periods
        )
    }
}
