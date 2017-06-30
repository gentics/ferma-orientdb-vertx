// The GIT repository for this pipeline lib is defined in the global Jenkins setting
@Library('jenkins-pipeline-library')
import com.gentics.*

// Make the helpers aware of this jobs environment
JobContext.set(this)

properties([
        parameters([
                booleanParam(name: 'runTests',            defaultValue: true,  description: "Whether to run the unit tests"),
                booleanParam(name: 'runRelease',           defaultValue: false, description: "Whether to run the release steps.")
        ])
])

final def gitCommitTag         = '[Jenkins | ' + env.JOB_BASE_NAME + ']';

node('jenkins-slave') {
  sshagent(["git"]) {
    def version = null
    def branchName = null

    stage('Checkout') {
      checkout scm
    }

    stage('Preparation') {
      branchName = GitHelper.fetchCurrentBranchName()
      version = MavenHelper.getVersion()
      if (Boolean.valueOf(params.runRelease)) {
        version = MavenHelper.transformSnapshotToReleaseVersion(version)
        MavenHelper.setVersion(version)
      }
    }

    stage("Test") {
      if (Boolean.valueOf(params.runTests)) {
        try {
          sh "mvn -B clean test -Dmaven.test.failure.ignore"
        } finally {
          junit  "**/target/surefire-reports/*.xml"
        }
      } else {
        echo "Skipped.."
      }
    }

    stage("Build") {
      sh "mvn -B clean deploy -DskipTests"
    }

    stage('Post Build') {
      if (Boolean.valueOf(params.runRelease)) {
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





