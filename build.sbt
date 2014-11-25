name := "dst-solr"

organization := "se.destination"

version := "1.1-SNAPSHOT"

publishTo := {
  val repoPath = Path.userHome.absolutePath + "/Dropbox/Destination/dst_maven"
  if (version.value.trim.endsWith("SNAPSHOT"))
    Some(Resolver.file("file", new File(repoPath + "/snapshots"))(Resolver.ivyStylePatterns))
  else
    Some(Resolver.file("file", new File(repoPath + "/releases"))(Resolver.ivyStylePatterns))
}

scalacOptions ++= Seq(
  "-feature"
)

play.Project.playScalaSettings
