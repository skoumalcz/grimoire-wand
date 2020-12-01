package com.skoumal.grimoire.build

import com.android.build.gradle.BaseExtension
import com.jfrog.bintray.gradle.BintrayExtension
import org.gradle.api.Project
import org.gradle.api.provider.Property
import org.gradle.api.publish.internal.DefaultPublishingExtension
import org.gradle.api.publish.maven.MavenPublication
import org.gradle.api.tasks.javadoc.Javadoc
import org.gradle.jvm.tasks.Jar
import org.gradle.kotlin.dsl.*

class LibraryPublishing(
    private val project: Project,
    private val publishing: DefaultPublishingExtension =
        project.extensions.findByName("publishing") as DefaultPublishingExtension,
    private val bintray: BintrayExtension =
        project.extensions.findByName("bintray") as BintrayExtension
) {

    private lateinit var androidJavadocJar: Jar
    private lateinit var androidSourcesJar: Jar

    fun addJavadocTask() = apply {
        project.configure<BaseExtension> {
            val androidJavadoc = project.task("androidJavadoc", Javadoc::class) {
                source = sourceSets["main"].java.getSourceFiles()
            }
            androidJavadocJar = project.task("androidJavadocJar", Jar::class) {
                archiveClassifier.set("javadoc")
                from(androidJavadoc.destinationDir)
            }
            androidSourcesJar = project.task("androidSourcesJar", Jar::class) {
                archiveClassifier.set("sources")
                from(sourceSets["main"].java.getSourceFiles())
            }
        }
    }

    /**
     * @param groupId example: `com.skoumal.grimoire`
     * @param version example: `1.0.0-alpha01`
     * @param name example: `Grimoire Cover`
     * @param description example: `Lorem ipsum dolor sit amet`
     * @param url example: `https://github.com/skoumalcz/grimoire-cover`
     * @param licence example: `GPL-3.0`
     * @param licenceUrl example: `https://www.gnu.org/licenses/gpl-3.0.en.html`
     * @param developerId example: `johndoe`
     * @param developerName example: `John Doe`
     * @param developerEmail example: `johndoe@email.com`
     * @param scmConnection example: `scm:git://github.com/skoumalcz/grimoire-cover.git`
     * */
    fun applyPublication(publicationName: String = "bintrayPublication") = apply {
        publishing.publications {
            create(publicationName, MavenPublication::class.java).apply {
                from(project.components.findByName("release"))

                artifact(androidJavadocJar)
                artifact(androidSourcesJar)

                groupId = project.getStringProperty("groupId")
                artifactId = project.name
                version = project.getStringProperty("version")

                pom {
                    name += project.getStringProperty("name")
                    description += project.getStringProperty("description")
                    url += project.getStringProperty("url")

                    licenses {
                        license {
                            name += project.getStringProperty("licence")
                            url += project.getStringProperty("licenceUrl")
                        }
                    }

                    developers {
                        developer {
                            id += project.getStringProperty("developerId")
                            name += project.getStringProperty("developerName")
                            email += project.getStringProperty("developerEmail")
                        }
                    }

                    scm {
                        connection += project.getStringProperty("scmConnection")
                        developerConnection += project.getStringProperty("scmConnection")
                        url += project.getStringProperty("url")
                    }
                }
            }
        }
    }

    /**
     * @param bintrayUser example: `johndoe`
     * @param bintrayKey example: `894d4946a4a23132839752007ab`
     * @param bintrayRepo example: `grimoire`
     * @param url example: `https://github.com/skoumalcz/grimoire-cover`
     * @param licence example: `GPL-3.0`
     * @param description example: `Lorem ipsum dolor sit amet`
     * @param version example: `1.0.0-alpha01`
     * */
    fun applyBintrayOnPublication(publicationName: String = "bintrayPublication") = apply {
        bintray.apply {
            user = project.getNullableStringProperty("bintrayUser") ?: System.getenv("BINTRAY_USER")
            key = project.getNullableStringProperty("bintrayKey") ?: System.getenv("BINTRAY_KEY")

            setPublications(publicationName)

            pkg = PackageConfig().apply {
                repo = project.getStringProperty("bintrayRepo")
                name = project.name
                userOrg = user
                vcsUrl = project.getStringProperty("url")
                publicDownloadNumbers = true

                setLicenses(project.getStringProperty("licence"))

                version = VersionConfig().apply {
                    name = project.getStringProperty("version")
                    desc = project.getStringProperty("description")
                    vcsTag = project.getStringProperty("version")
                }
            }
        }
    }

    private fun Project.getStringProperty(name: String) = properties[name] as String
    private fun Project.getNullableStringProperty(name: String) = properties[name] as? String

    private operator fun <T> Property<T>.plusAssign(t: T) = set(t)

}