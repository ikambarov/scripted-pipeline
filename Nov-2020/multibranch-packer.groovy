node {
    def aws_region_var = ''

    if(env.BRANCH_NAME ==~ "dev.*"){
        println("Applying for dev")
        aws_region_var = 'us-east-1'
    }
    else if(env.BRANCH_NAME ==~ "qa.*"){
        println("Applying for qa")
        aws_region_var = 'us-east-2'
    }
    else if(env.BRANCH_NAME ==~ "master"){
        println("Applying for prod")
        aws_region_var = 'us-west-2'
    }

    stage("Pull Repo"){
        git branch: 'dev-feature-test', url: 'https://github.com/ikambarov/packer.git'
    }

    withCredentials([usernamePassword(credentialsId: 'jenkins-aws-access-key', passwordVariable: 'AWS_SECRET_ACCESS_KEY', usernameVariable: 'AWS_ACCESS_KEY_ID')]) {
        withEnv(["AWS_REGION=${aws_region_var}", "PACKER_AMI_NAME=apache-${UUID.randomUUID().toString()}"])  {
            stage("Packer Validate"){
                sh """
                    packer validate apache.json
                """        
            }
            stage("Packer Build"){
                sh """
                    packer build apache.json 
                """
            }
        }
    }
}