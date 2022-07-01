node("terraform"){
    stage("Pull Repo"){
        git 'https://github.com/ikambarov/terraform-vpc.git'
    }

    withCredentials([usernamePassword(credentialsId: 'aws-creds', passwordVariable: 'AWS_SECRET_ACCESS_KEY', usernameVariable: 'AWS_ACCESS_KEY_ID')]) {
        stage("Init"){
            sh """
                bash setenv.sh dev.tfvars
                terraform init
            """
        }
        
        stage("Plan"){
            sh """
                terraform plan -var-file dev.tfvars
            """
        }

        stage("Apply"){
            sh """
                terraform apply -var-file dev.tfvars -auto-approve
            """
        }
    }
}