package com.example.data.repository

import com.example.data.model.MediaItem
import com.example.data.model.MediaType
import com.example.data.model.MediaUpdate
import com.example.data.model.ReleaseStatus
import java.util.Calendar

object MediaCatalog {
    // Current base: Oct 20, 2026
    private fun getEpochForDate(year: Int, month0Indexed: Int, day: Int): Long {
        return Calendar.getInstance().apply {
            set(year, month0Indexed, day, 18, 0, 0)
            set(Calendar.MILLISECOND, 0)
        }.timeInMillis
    }

    val items: List<MediaItem> = listOf(
        MediaItem(
            id = "stranger_things_5",
            title = "Stranger Things (Season 5)",
            type = MediaType.SERIES,
            releaseDateEpoch = getEpochForDate(2026, 9, 30), // Oct 30, 2026 (~10 days)
            releaseDateDisplay = "Oct 30, 2026",
            status = ReleaseStatus.PREMIERING_SOON,
            posterUrl = "https://images.unsplash.com/photo-1518709268805-4e9042af9f23?w=600&auto=format&fit=crop&q=80",
            backdropUrl = "https://images.unsplash.com/photo-1509198397868-475647b2a1e5?w=1200&auto=format&fit=crop&q=80",
            platform = "Netflix",
            genres = listOf("Sci-Fi", "Horror", "Drama", "Mystery"),
            hypeScore = 99,
            rating = 9.2,
            synopsis = "The final battle for Hawkins begins. Eleven and the gang enter the heart of the Upside Down to confront Vecna once and for all as the barrier between dimensions crumbles.",
            directorOrCreator = "The Duffer Brothers",
            cast = listOf("Millie Bobby Brown", "Finn Wolfhard", "David Harbour", "Winona Ryder"),
            trailerQuery = "Stranger Things Season 5 official trailer",
            seasons = 5,
            episodes = 8,
            latestUpdate = MediaUpdate(
                id = "up_st5_1",
                mediaId = "stranger_things_5",
                mediaTitle = "Stranger Things (Season 5)",
                updateType = "Final Trailer",
                headline = "Full Hawkins Apocalypse Trailer Released!",
                summary = "Netflix unveiled the intense 3-minute final trailer teasing Eleven's confrontation with Vecna. Clocks are ticking across Hawkins.",
                timestampDaysAgo = 1,
                isHot = true
            )
        ),
        MediaItem(
            id = "the_batman_2",
            title = "The Batman Part II",
            type = MediaType.MOVIE,
            releaseDateEpoch = getEpochForDate(2026, 10, 6), // Nov 6, 2026 (~17 days)
            releaseDateDisplay = "Nov 6, 2026",
            status = ReleaseStatus.PREMIERING_SOON,
            posterUrl = "https://images.unsplash.com/photo-1509281373149-e957c6296406?w=600&auto=format&fit=crop&q=80",
            backdropUrl = "https://images.unsplash.com/photo-1478760329108-5c3ed9d495a0?w=1200&auto=format&fit=crop&q=80",
            platform = "Theaters & IMAX",
            genres = listOf("Action", "Crime", "Mystery", "Thriller"),
            hypeScore = 97,
            rating = 9.0,
            synopsis = "As winter grips Gotham City, Bruce Wayne digs deeper into the shadowy web of corruption following the flood, facing a calculating new threat in Arkham.",
            directorOrCreator = "Matt Reeves",
            cast = listOf("Robert Pattinson", "Zoë Kravitz", "Colin Farrell", "Jeffrey Wright"),
            trailerQuery = "The Batman Part 2 official trailer",
            latestUpdate = MediaUpdate(
                id = "up_bm2_1",
                mediaId = "the_batman_2",
                mediaTitle = "The Batman Part II",
                updateType = "Runtime Confirmed",
                headline = "IMAX Expanded Ratio & 168-minute Runtime Confirmed",
                summary = "Director Matt Reeves confirmed the cinematic cut will feature 45 minutes of IMAX 1.43:1 aspect ratio sequences.",
                timestampDaysAgo = 2,
                isHot = true
            )
        ),
        MediaItem(
            id = "spider_verse_3",
            title = "Spider-Man: Beyond the Spider-Verse",
            type = MediaType.MOVIE,
            releaseDateEpoch = getEpochForDate(2026, 11, 25), // Dec 25, 2026
            releaseDateDisplay = "Dec 25, 2026",
            status = ReleaseStatus.UPCOMING,
            posterUrl = "https://images.unsplash.com/photo-1635805737707-575885ab0820?w=600&auto=format&fit=crop&q=80",
            backdropUrl = "https://images.unsplash.com/photo-1607604276583-eef5d076aa5f?w=1200&auto=format&fit=crop&q=80",
            platform = "Theaters & Dolby Cinema",
            genres = listOf("Animation", "Action", "Sci-Fi", "Superhero"),
            hypeScore = 98,
            rating = 9.3,
            synopsis = "Miles Morales must escape Earth-42 while Gwen Stacy gathers a team of renegade Spider-heroes across alternate realities to save him and preserve the Multiverse canon.",
            directorOrCreator = "Joaquim Dos Santos, Kemp Powers",
            cast = listOf("Shameik Moore", "Hailee Steinfeld", "Oscar Isaac", "Daniel Kaluuya"),
            trailerQuery = "Spider-Man Beyond the Spider-Verse trailer",
            latestUpdate = MediaUpdate(
                id = "up_sv3_1",
                mediaId = "spider_verse_3",
                mediaTitle = "Spider-Man: Beyond the Spider-Verse",
                updateType = "Soundtrack & First Look",
                headline = "Metro Boomin Returns for Official Original Score",
                summary = "Sony Pictures previewed a 4-minute continuous animation sequence blending five distinct comic art styles simultaneously.",
                timestampDaysAgo = 3,
                isHot = true
            )
        ),
        MediaItem(
            id = "severance_2",
            title = "Severance (Season 2)",
            type = MediaType.SERIES,
            releaseDateEpoch = getEpochForDate(2026, 10, 15), // Nov 15, 2026
            releaseDateDisplay = "Nov 15, 2026",
            status = ReleaseStatus.PREMIERING_SOON,
            posterUrl = "https://images.unsplash.com/photo-1579783900882-c0d3dad7b119?w=600&auto=format&fit=crop&q=80",
            backdropUrl = "https://images.unsplash.com/photo-1516321318423-f06f85e504b3?w=1200&auto=format&fit=crop&q=80",
            platform = "Apple TV+",
            genres = listOf("Psychological Thriller", "Sci-Fi", "Mystery"),
            hypeScore = 96,
            rating = 9.1,
            synopsis = "Mark and his macrodata refinement colleagues confront the terrifying repercussions of their overtime contingency breakthrough on the outside world.",
            directorOrCreator = "Ben Stiller",
            cast = listOf("Adam Scott", "Patricia Arquette", "John Turturro", "Britt Lower"),
            trailerQuery = "Severance Season 2 official teaser",
            seasons = 2,
            episodes = 10,
            latestUpdate = MediaUpdate(
                id = "up_sev2_1",
                mediaId = "severance_2",
                mediaTitle = "Severance (Season 2)",
                updateType = "Teaser Drop",
                headline = "Lumon Industries Re-Onboarding Teaser Released",
                summary = "A cryptic Lumon internal memo teaser shows Mark Scout returning to the severed floor with new security protocols in place.",
                timestampDaysAgo = 4,
                isHot = false
            )
        ),
        MediaItem(
            id = "avatar_3",
            title = "Avatar: Fire and Ash",
            type = MediaType.MOVIE,
            releaseDateEpoch = getEpochForDate(2026, 11, 18), // Dec 18, 2026
            releaseDateDisplay = "Dec 18, 2026",
            status = ReleaseStatus.UPCOMING,
            posterUrl = "https://images.unsplash.com/photo-1534447677768-be436bb09401?w=600&auto=format&fit=crop&q=80",
            backdropUrl = "https://images.unsplash.com/photo-1506744038136-46273834b3fb?w=1200&auto=format&fit=crop&q=80",
            platform = "Theaters & IMAX 3D",
            genres = listOf("Sci-Fi", "Adventure", "Fantasy", "Action"),
            hypeScore = 95,
            rating = 8.8,
            synopsis = "Jake Sully and Neytiri journey across unexplored volcanic regions of Pandora, coming into conflict with the fiery 'Ash People' Na'vi clan.",
            directorOrCreator = "James Cameron",
            cast = listOf("Sam Worthington", "Zoe Saldana", "Sigourney Weaver", "Oona Chaplin"),
            trailerQuery = "Avatar Fire and Ash official teaser",
            latestUpdate = MediaUpdate(
                id = "up_av3_1",
                mediaId = "avatar_3",
                mediaTitle = "Avatar: Fire and Ash",
                updateType = "Official Stills",
                headline = "First Look at the Ash People Clan Revealed",
                summary = "James Cameron revealed groundbreaking volcanic biome concepts and new subterranean Pandora creatures.",
                timestampDaysAgo = 5,
                isHot = true
            )
        ),
        MediaItem(
            id = "dune_prophecy_2",
            title = "Dune: Prophecy (Season 2)",
            type = MediaType.SERIES,
            releaseDateEpoch = getEpochForDate(2026, 10, 20), // Nov 20, 2026
            releaseDateDisplay = "Nov 20, 2026",
            status = ReleaseStatus.UPCOMING,
            posterUrl = "https://images.unsplash.com/photo-1509198397868-475647b2a1e5?w=600&auto=format&fit=crop&q=80",
            backdropUrl = "https://images.unsplash.com/photo-1446776811953-b23d57bd21aa?w=1200&auto=format&fit=crop&q=80",
            platform = "HBO Max",
            genres = listOf("Sci-Fi", "Drama", "Fantasy"),
            hypeScore = 93,
            rating = 8.7,
            synopsis = "Ten thousand years before Paul Atreides, two Harkonnen sisters combat unseen forces that threaten the future of humankind and birth the legendary Bene Gesserit.",
            directorOrCreator = "Alison Schapker",
            cast = listOf("Emily Watson", "Olivia Williams", "Jodhi May", "Mark Strong"),
            trailerQuery = "Dune Prophecy Season 2 trailer",
            seasons = 2,
            episodes = 8,
            latestUpdate = MediaUpdate(
                id = "up_dp2_1",
                mediaId = "dune_prophecy_2",
                mediaTitle = "Dune: Prophecy (Season 2)",
                updateType = "Casting News",
                headline = "Emperor Corrino Succession Battle Arc Detailed",
                summary = "Showrunners tease political espionage and the early spice guild monopoly machinations on Arrakis.",
                timestampDaysAgo = 6,
                isHot = false
            )
        ),
        MediaItem(
            id = "avengers_secret_wars",
            title = "Avengers: Secret Wars",
            type = MediaType.MOVIE,
            releaseDateEpoch = getEpochForDate(2027, 4, 7), // May 7, 2027
            releaseDateDisplay = "May 7, 2027",
            status = ReleaseStatus.UPCOMING,
            posterUrl = "https://images.unsplash.com/photo-1612036782180-6f0b6cd846fe?w=600&auto=format&fit=crop&q=80",
            backdropUrl = "https://images.unsplash.com/photo-1579783900882-c0d3dad7b119?w=1200&auto=format&fit=crop&q=80",
            platform = "Theaters & IMAX",
            genres = listOf("Action", "Sci-Fi", "Superhero", "Adventure"),
            hypeScore = 99,
            rating = 9.4,
            synopsis = "The Marvel Multiverse collides into Battleworld. Heroes from every universe join together in the ultimate struggle to save existence itself.",
            directorOrCreator = "Russo Brothers",
            cast = listOf("Robert Downey Jr.", "Pedro Pascal", "Benedict Cumberbatch", "Anthony Mackie"),
            trailerQuery = "Avengers Secret Wars announcement",
            latestUpdate = MediaUpdate(
                id = "up_sw_1",
                mediaId = "avengers_secret_wars",
                mediaTitle = "Avengers: Secret Wars",
                updateType = "Production Milestone",
                headline = "Doctor Doom First Look and Battleworld Set Designs",
                summary = "Marvel Studios showcased concept art for the sprawling patchwork kingdom of Battleworld at New York Comic Con.",
                timestampDaysAgo = 7,
                isHot = true
            )
        ),
        MediaItem(
            id = "euphoria_3",
            title = "Euphoria (Season 3)",
            type = MediaType.SERIES,
            releaseDateEpoch = getEpochForDate(2027, 0, 15), // Jan 15, 2027
            releaseDateDisplay = "Jan 15, 2027",
            status = ReleaseStatus.UPCOMING,
            posterUrl = "https://images.unsplash.com/photo-1492691527719-9d1e07e534b4?w=600&auto=format&fit=crop&q=80",
            backdropUrl = "https://images.unsplash.com/photo-1514525253161-7a46d19cd819?w=1200&auto=format&fit=crop&q=80",
            platform = "HBO Max",
            genres = listOf("Drama", "Mystery", "Crime"),
            hypeScore = 91,
            rating = 8.5,
            synopsis = "A 5-year time jump follows Rue Bennett into early adulthood as she navigates sobriety, creative ambition, and past debts in Los Angeles.",
            directorOrCreator = "Sam Levinson",
            cast = listOf("Zendaya", "Sydney Sweeney", "Hunter Schafer", "Jacob Elordi"),
            trailerQuery = "Euphoria Season 3 teaser",
            seasons = 3,
            episodes = 8,
            latestUpdate = MediaUpdate(
                id = "up_eup_1",
                mediaId = "euphoria_3",
                mediaTitle = "Euphoria (Season 3)",
                updateType = "Sneak Peek",
                headline = "Zendaya Teases Noir Cinematic Aesthetic for Season 3",
                summary = "Filming in 35mm film stock will feature neo-noir cinematography moving away from high school hallways to urban LA.",
                timestampDaysAgo = 9,
                isHot = false
            )
        ),
        MediaItem(
            id = "last_of_us_2",
            title = "The Last of Us (Season 2)",
            type = MediaType.SERIES,
            releaseDateEpoch = getEpochForDate(2027, 1, 12), // Feb 12, 2027
            releaseDateDisplay = "Feb 12, 2027",
            status = ReleaseStatus.UPCOMING,
            posterUrl = "https://images.unsplash.com/photo-1518709268805-4e9042af9f23?w=600&auto=format&fit=crop&q=80",
            backdropUrl = "https://images.unsplash.com/photo-1509198397868-475647b2a1e5?w=1200&auto=format&fit=crop&q=80",
            platform = "HBO Max",
            genres = listOf("Drama", "Post-Apocalyptic", "Horror", "Adventure"),
            hypeScore = 97,
            rating = 9.2,
            synopsis = "Five years of peace in Jackson are shattered by a tragic act of violence. Ellie embarks on a relentless path of vengeance across the rainy ruins of Seattle.",
            directorOrCreator = "Craig Mazin, Neil Druckmann",
            cast = listOf("Pedro Pascal", "Bella Ramsey", "Kaitlyn Dever", "Isabela Merced"),
            trailerQuery = "The Last of Us Season 2 trailer",
            seasons = 2,
            episodes = 7,
            latestUpdate = MediaUpdate(
                id = "up_tlou_1",
                mediaId = "last_of_us_2",
                mediaTitle = "The Last of Us (Season 2)",
                updateType = "Teaser Drop",
                headline = "Official Seattle Seraphites Teaser Released",
                summary = "HBO gave the first glimpse of Abby and the Seraphites in a devastating look at Seattle's faction war.",
                timestampDaysAgo = 4,
                isHot = true
            )
        ),
        MediaItem(
            id = "blade_runner_2099",
            title = "Blade Runner 2099",
            type = MediaType.SERIES,
            releaseDateEpoch = getEpochForDate(2027, 2, 20), // Mar 20, 2027
            releaseDateDisplay = "Mar 20, 2027",
            status = ReleaseStatus.UPCOMING,
            posterUrl = "https://images.unsplash.com/photo-1509281373149-e957c6296406?w=600&auto=format&fit=crop&q=80",
            backdropUrl = "https://images.unsplash.com/photo-1508739773434-c26b3d09e071?w=1200&auto=format&fit=crop&q=80",
            platform = "Amazon Prime Video",
            genres = listOf("Cyberpunk", "Sci-Fi", "Neo-Noir", "Mystery"),
            hypeScore = 92,
            rating = 8.8,
            synopsis = "Fifty years after the blackout of 2049, a fugitive Replicant with minutes left to live uncovers an existential corporate conspiracy in Los Angeles.",
            directorOrCreator = "Ridley Scott, Silka Luisa",
            cast = listOf("Michelle Yeoh", "Hunter Schafer", "Dimitri Abold"),
            trailerQuery = "Blade Runner 2099 prime video teaser",
            seasons = 1,
            episodes = 8,
            latestUpdate = MediaUpdate(
                id = "up_br_1",
                mediaId = "blade_runner_2099",
                mediaTitle = "Blade Runner 2099",
                updateType = "First Look",
                headline = "Michelle Yeoh's Replicant Character Olwen Unveiled",
                summary = "Prime Video released character portraits showing atmospheric neon-drenched retro-futurist Los Angeles sets.",
                timestampDaysAgo = 8,
                isHot = false
            )
        ),
        MediaItem(
            id = "peaky_blinders_movie",
            title = "Peaky Blinders: The Immortal Man",
            type = MediaType.MOVIE,
            releaseDateEpoch = getEpochForDate(2026, 11, 11), // Dec 11, 2026
            releaseDateDisplay = "Dec 11, 2026",
            status = ReleaseStatus.UPCOMING,
            posterUrl = "https://images.unsplash.com/photo-1485846234645-a62644f84728?w=600&auto=format&fit=crop&q=80",
            backdropUrl = "https://images.unsplash.com/photo-1536440136628-849c177e76a1?w=1200&auto=format&fit=crop&q=80",
            platform = "Netflix & Limited Theaters",
            genres = listOf("Crime", "Drama", "History", "Action"),
            hypeScore = 95,
            rating = 9.0,
            synopsis = "Tommy Shelby is pulled back from isolation on the eve of World War II as British Intelligence requests his Birmingham underworld machinery to fight covert fascism.",
            directorOrCreator = "Steven Knight, Tom Harper",
            cast = listOf("Cillian Murphy", "Barry Keoghan", "Rebecca Ferguson", "Tim Roth"),
            trailerQuery = "Peaky Blinders movie trailer",
            latestUpdate = MediaUpdate(
                id = "up_pb_1",
                mediaId = "peaky_blinders_movie",
                mediaTitle = "Peaky Blinders: The Immortal Man",
                updateType = "Production Wrap",
                headline = "Cillian Murphy Wraps Filming in Birmingham Digbeth",
                summary = "Steven Knight announced production has officially completed on the explosive feature film finale.",
                timestampDaysAgo = 10,
                isHot = true
            )
        ),
        MediaItem(
            id = "gta6_docuseries",
            title = "Grand Theft Auto VI: The Story Behind",
            type = MediaType.SERIES,
            releaseDateEpoch = getEpochForDate(2026, 10, 5), // Nov 5, 2026 (~16 days)
            releaseDateDisplay = "Nov 5, 2026",
            status = ReleaseStatus.PREMIERING_SOON,
            posterUrl = "https://images.unsplash.com/photo-1542751371-adc38448a05e?w=600&auto=format&fit=crop&q=80",
            backdropUrl = "https://images.unsplash.com/photo-1511512578047-dfb367046420?w=1200&auto=format&fit=crop&q=80",
            platform = "YouTube & Streaming",
            genres = listOf("Documentary", "Gaming", "Crime", "Culture"),
            hypeScore = 98,
            rating = 9.4,
            synopsis = "An exclusive deep dive into the 12-year development of Vice City's return, featuring interviews with animators, composers, and game directors.",
            directorOrCreator = "Rockstar & NoClip Studios",
            cast = listOf("Sam Houser", "Vice City Composers", "Motion Capture Artists"),
            trailerQuery = "GTA 6 behind the scenes trailer",
            seasons = 1,
            episodes = 6,
            latestUpdate = MediaUpdate(
                id = "up_gta_1",
                mediaId = "gta6_docuseries",
                mediaTitle = "Grand Theft Auto VI: The Story Behind",
                updateType = "Teaser Drop",
                headline = "Episode 1 'Neon Dreams' Premiering in 16 Days",
                summary = "First episode covers the simulation physics and satirical culture created for modern Leonida.",
                timestampDaysAgo = 2,
                isHot = true
            )
        )
    )

    val allUpdates: List<MediaUpdate> = items.mapNotNull { it.latestUpdate }.sortedBy { it.timestampDaysAgo }
}
