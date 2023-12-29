plugins {
    kotlin("jvm") version "1.9.20"
    kotlin("plugin.serialization") version "1.9.20"
}

repositories {
    mavenCentral()
}

tasks {
    test {
        useJUnitPlatform()
    }
}

dependencies {
//    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.4.1") {
//        because("see Aoc Day 13")
//    }
//    implementation("com.github.shiguruikai:combinatoricskt:1.6.0") {
//        because("I need combinations of sets for Day 16 and this was a bug-free way to do it")
//    }
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.8.0-RC") {
        because("Day 05")
    }
    implementation("org.jgrapht:jgrapht-core:1.5.2") {
        because("Day 25 min-cut")
    }

    testImplementation("org.junit.jupiter:junit-jupiter:5.9.0")
    testImplementation("org.assertj:assertj-core:3.23.1")
}
