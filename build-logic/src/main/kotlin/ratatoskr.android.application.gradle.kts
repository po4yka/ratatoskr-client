import com.android.build.api.dsl.ApplicationExtension

extensions.configure<ApplicationExtension> {
    namespace = "com.po4yka.ratatoskr"
    compileSdk = 36

    defaultConfig {
        minSdk = 24
        targetSdk = 36
    }

    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
            excludes += "/META-INF/INDEX.LIST"
            excludes += "/META-INF/io.netty.versions.properties"
        }
    }

    compileOptions {
        isCoreLibraryDesugaringEnabled = true
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
}
