node{
    properties([
        parameters([
            string(defaultValue: '', description: 'Provide Remote Node IP', name: 'node_ip', trim: true)
        ])
    ])

    withCredentials([sshUserPrivateKey(credentialsId: 'jenkins-ssh-key', keyFileVariable: 'SSH_KEY', passphraseVariable: '', usernameVariable: 'SSH_USERNAME')]) {
    stage("Ping"){
            sh """
                export ANSIBLE_HOST_KEY_CHECKING=False
                ansible -i "${node_ip}," --private-key $SSH_KEY all -m ping -u $SSH_USERNAME
            """
        } 
    } 

