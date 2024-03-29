properties([
    parameters([
        string(description: 'Enter Linux IP Address', name: 'IP', trim: true)
        ])
    ])

node {
    stage('Connect to Test VM') {
        withCredentials([sshUserPrivateKey(credentialsId: 'jenkins-key', keyFileVariable: 'SSH_KEY', usernameVariable: 'SSH_USERNAME')]) {
            sh "ssh -i $SSH_KEY -o StrictHostKeyChecking=no $SSH_USERNAME@$params.IP"
        }
    }
}
