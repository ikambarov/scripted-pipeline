properties([
    parameters([
        string(description: 'Enter an IP address:', name: 'ip', trim: true)
    ])
])

node('ansible'){
    stage("Pull Repo"){
        git 'https://github.com/ikambarov/ansible-melodi.git'
    }

    stage("Deploy"){
        withCredentials([sshUserPrivateKey(credentialsId: 'jenkins-key', keyFileVariable: 'SSH_KEY', usernameVariable: 'SSH_USER')]) {
            sh """
                export ANSIBLE_HOST_KEY_CHECKING=False
                ansible-playbook --private-key $SSH_KEY -i '${ params.ip },' -u $SSH_USER main.yml
            """
        }
    }
}
