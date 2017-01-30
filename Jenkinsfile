// The GIT repository for this pipeline lib is defined in the global Jenkins setting
@Library('jenkins-pipeline-library')
import com.gentics.*

// Make the helpers aware of this jobs environment
JobContext.set(this)

GenericHelper.setParameterDefinitions([
  new BooleanParameterDefinition('runTests',         true, "Whether to run the unit tests"),
  new BooleanParameterDefinition('runRelease',       false, "Whether to run the release steps.")
])

final def sshAgent             = "601b6ce9-37f7-439a-ac0b-8e368947d98d"
final def gitCommitTag         = '[Jenkins | ' + env.JOB_BASE_NAME + ']';

node('dockerSlave') {
  sshagent([sshAgent]) {
    final def mvnHome = tool 'M3'
    def version = null
    def branchName = null

    stage('Checkout') {
      sh "rm -rf *; rm -rf .git"
      checkout scm
    }

    stage('Preparation') {
      branchName = GitHelper.fetchCurrentBranchName()
      version = MavenHelper.getVersion()
      if (Boolean.valueOf(runRelease)) {
        version = MavenHelper.transformSnapshotToReleaseVersion(version)
        MavenHelper.setVersion(version)
      }
    }

    stage("Test") {
      if (Boolean.valueOf(runTests)) {
        try {
          sh "${mvnHome}/bin/mvn -B clean test -Dmaven.test.failure.ignore"
        } finally {
          junit  "**/target/surefire-reports/*.xml"
        }
      } else {
        echo "Skipped.."
      }
    }

    stage("Build") {
      sh "${mvnHome}/bin/mvn -B clean deploy -DskipTests"
    }

    stage('Post Build') {
      if (Boolean.valueOf(runRelease)) {
        GitHelper.addCommit('.', gitCommitTag + ' Committing release changes (' + version + ')')
        GitHelper.addTag(version, 'Release of version ' + version)
        GitHelper.pushTag(version)
        version = MavenHelper.getNextSnapShotVersion(version)
        MavenHelper.setVersion(version)
        GitHelper.addCommit('.', gitCommitTag + ' Prepare for the next development iteration (' + version + ')')
        GitHelper.pushBranch(branchName)
      }
    }
  }
}





