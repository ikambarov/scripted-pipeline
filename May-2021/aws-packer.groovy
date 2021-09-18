node("worker1"){
    stage("Pull code"){
        git 'https://github.com/ikambarov/packer.git'
    }

    withEnv(['AWS_REGION=us-east-1', 'PACKER_AMI_NAME=testimage1']) {
        withCredentials([usernamePassword(credentialsId: 'aws-key', passwordVariable: 'AWS_SECRET_ACCESS_KEY', usernameVariable: 'AWS_ACCESS_KEY_ID')]) {

            stage("Validate"){
                sh '''
                    packer validate apache.json
                '''
            }

            stage("Build"){
                sh '''
                    packer build apache.json
                '''
            }
        }
    }
}