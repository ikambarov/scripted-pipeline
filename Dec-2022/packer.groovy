node {
    stage("Pull Repo"){
        git 'https://github.com/ikambarov/packer.git'
    }

    withCredentials([usernamePassword(credentialsId: 'aws-key', passwordVariable: 'AWS_SECRET_ACCESS_KEY', usernameVariable: 'AWS_ACCESS_KEY_ID')]) {
        withEnv(['AWS_REGION=us-east-1', 'PACKER_AMI_NAME=apache']) {
            stage("Validate"){
                sh """
                    packer validate apache.json
                """
            }

            stage("Build"){
                sh """
                    packer build apache.json
                """
            }
        }
    }
}