properties([
    parameters([
        string(description: 'Enter Linux IP Address', name: 'IP', trim: true)
        ])
    ])

node {
    stage("Test"){
        withCredentials([sshUserPrivateKey(credentialsId: 'jenkins-key', keyFileVariable: 'SSH_KEY', usernameVariable: 'SSH_USERNAME')]) {
            sh "ansible -m ping --private-key=$SSH_KEY all -i '$params.IP,' -u $SSH_USERNAME"
        }
    }
}
