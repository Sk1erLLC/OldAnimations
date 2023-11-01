plugins {
    kotlin("jvm") version "1.9.20" apply false
    id("gg.essential.multi-version.root")
}

// Loom tries to find the active mixin version by recursing up to the root project and checking each project's
// compileClasspath and build script classpath (in that order). Since we've loom in our root project's classpath,
// loom will only find it after checking the root project's compileClasspath (which doesn't exist by default).
configurations.register("compileClasspath")

preprocess {
    val forge10809 = createNode("1.8.9-forge", 10809, "srg")
}
