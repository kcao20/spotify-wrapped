// Top-level build file where you can add configuration options common to all sub-projects/modules.
plugins {
    alias(libs.plugins.androidApplication) apply false
    id("com.diffplug.spotless") version "6.25.0"
}

spotless {
    ratchetFrom("origin/main")

    format("misc") {
        target("*.gradle", ".gitattributes", ".gitignore")

        trimTrailingWhitespace()
        indentWithSpaces()
        endWithNewline()
    }

    java {
        target("app/src/*/java/**/*.java")

        importOrder()
        removeUnusedImports()
        cleanthat()

        palantirJavaFormat("2.40.0").style("AOSP").formatJavadoc(true)

        formatAnnotations()
    }
}
