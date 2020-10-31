node{
    withCredentials([sshUserPrivateKey(credentialsId: 'jenkins-ssh-key', keyFileVariable: 'SSH_KEY', passphraseVariable: '', usernameVariable: 'SSH_USERNAME')]) {
       stage("Ping"){
            sh """
                ansible -i "162.243.167.53," --private-key $SSH_KEY all -m ping -u $SSH_USERNAME
            """
        } 
    }  
}
