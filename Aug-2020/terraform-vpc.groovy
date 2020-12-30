node("terraform"){
    stage("Pull Repo"){
        git url: 'https://github.com/ikambarov/terraform-vpc.git'
    }

    withCredentials([usernamePassword(credentialsId: 'jenkins_aws_keys', passwordVariable: 'AWS_SECRET_ACCESS_KEY', usernameVariable: 'AWS_ACCESS_KEY_ID')]) {
        withEnv(['AWS_REGION=us-east-1']) {
            stage("Terrraform Init"){
                sh '''
                    source setenv.sh dev.tfvars
                    terraform init
                    terraform plan -var-file dev.tfvars
                '''
            }        
            
            stage("Terraform Apply"){
                sh '''
                    terraform apply -var-file dev.tfvars -auto-approve
                '''
            }
        }        
    }    
}
