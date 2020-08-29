node{
    stage("Pull Repo"){
        git url: 'https://github.com/ikambarov/terraform-vpc.git'
    }

    withCredentials([usernamePassword(credentialsId: 'jenkins-aws-access-key', passwordVariable: 'AWS_SECRET_ACCESS_KEY', usernameVariable: 'AWS_ACCESS_KEY_ID')]) {
        stage("Terrraform Init"){
            sh '''
                bash setenv.sh dev.tfvars
                export AWS_REGION=us-east-1
                terraform-0.13 init
                terraform-0.13 plan -var-file dev.tfvars
            '''
        }
        stage("Terraform Apply"){
            sh '''
                export AWS_REGION=us-east-1
                terraform-0.13 apply -var-file dev.tfvars -auto-approve
            '''
        }
    }    
}