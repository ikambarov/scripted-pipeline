properties([
    parameters([
        string(description: 'Enter IP Address', name: 'ip')
    ])
])

node {
    stage("Install Java"){
        withCredentials([sshUserPrivateKey(credentialsId: 'jenkins-master', keyFileVariable: 'SSH_PRIVATE_KEY', usernameVariable: 'SSH_USER')]) {
            sh "ssh -o StrictHostKeyChecking=no -i $SSH_PRIVATE_KEY $SSH_USER@${params.ip} yum install java-1.8.0-openjdk-devel -y"
        }
    }
}