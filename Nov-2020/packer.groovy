properties([
    parameters([
        string(defaultValue: '', description: 'Please enter AMI name:', name: 'ami_name', trim: true),
        choice(choices: ['dev', 'qa', 'prod'], description: 'Choose environment: ', name: 'environment')
        ])
    ])

node {
    def aws_region_var = ''

    if(params.environment == 'dev'){
        println("Applying for dev")
        aws_region_var = 'us-east-1'
    }
    else if(params.environment == 'qa'){
        println("Applying for qa")
        aws_region_var = 'us-east-2'
    }
    else{
        println("Applying for prod")
        aws_region_var = 'us-west-2'
    }

    stage("Pull Repo"){
        git branch: 'dev-feature-test', url: 'https://github.com/ikambarov/packer.git'
    }

    withCredentials([usernamePassword(credentialsId: 'jenkins-aws-access-key', passwordVariable: 'AWS_SECRET_ACCESS_KEY', usernameVariable: 'AWS_ACCESS_KEY_ID')]) {
        withEnv(["AWS_REGION=${aws_region_var}"]) {
            withEnv(["PACKER_AMI_NAME=${params.ami_name}"]) {
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
}