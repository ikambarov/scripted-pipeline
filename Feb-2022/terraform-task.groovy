node("terraform"){
    stage("Pull Repo"){
        git branch: 'solution', url: 'https://github.com/ikambarov/terraform-task.git'
    }

    stage("Init"){
        sh """
            ls -R 
            cd sandbox/
            terraform init
        """
    }

    withCredentials([usernamePassword(credentialsId: 'aws-creds', passwordVariable: 'AWS_SECRET_ACCESS_KEY', usernameVariable: 'AWS_ACCESS_KEY_ID')]) {
        stage("Plan"){
            sh """
                cd sandbox/
                terraform plan
            """
        }

        stage("Apply"){
            sh """
                cd sandbox/
                terraform apply -auto-approve
            """
        }
    }
}