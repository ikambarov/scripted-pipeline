node("terraform"){
    stage("Pull repo"){
        git branch: 'solution', url: 'https://github.com/ikambarov/terraform-task.git'
    }

    withCredentials([usernamePassword(credentialsId: 'jenkins_aws_keys', passwordVariable: 'AWS_SECRET_ACCESS_KEY', usernameVariable: 'AWS_ACCESS_KEY_ID')])
    {
        dir('sandbox') {
            stage("Init"){
                sh '''
                    terraform init
                '''
            }
            stage("Plan"){
                sh '''
                    terraform plan
                '''
            }
            stage("Apply"){
                sh '''
                    terraform apply -auto-approve 
                '''
            }
        }
    }
}

