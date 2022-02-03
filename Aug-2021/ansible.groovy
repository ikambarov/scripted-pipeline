node('ansible'){
    stage("Test"){
        withCredentials([sshUserPrivateKey(credentialsId: 'jenkins-key', keyFileVariable: 'SSH_KEY', usernameVariable: 'SSH_USER')]) {
            sh '''
                export ANSIBLE_HOST_KEY_CHECKING=False
                ansible --private-key $SSH_KEY -i "159.65.226.38," all -m ping -u $SSH_USER
            '''
        }
    }
}
