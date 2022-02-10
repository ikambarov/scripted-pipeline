if( env.BRANCH_NAME ==~ "dev.*" ){
    // Dev
    println("Build in Dev")
    aws_region_var = "us-east-1"
}
else if( env.BRANCH_NAME == "qa" ){
    // QA
    println("Build in QA")
    aws_region_var = "us-east-2"
}
else if( env.BRANCH_NAME == "master" ){
    // Prod
    println("Build in Prod")
    aws_region_var = "us-west-2"
}
else {
    error 'Branch name doesn\'t meet naming convention'
}

def aminame = "apache-${UUID.randomUUID().toString()}"

node('packer'){
    stage("Pull Template"){
        git 'https://github.com/ikambarov/packer.git'
    }

    withCredentials([usernamePassword(credentialsId: 'aws-key', passwordVariable: 'AWS_SECRET_ACCESS_KEY', usernameVariable: 'AWS_ACCESS_KEY_ID')]) {
        withEnv(["AWS_REGION=${aws_region_var}", "PACKER_AMI_NAME=${aminame}"]) {
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