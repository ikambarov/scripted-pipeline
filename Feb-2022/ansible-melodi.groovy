properties([
    parameters([
        string(description: 'Enter Linux IP Address', name: 'IP', trim: true)
        ])
    ])

node {
    stage("Pull Playbook"){
        

        git 'https://github.com/ikambarov/ansible-melodi.git'
    }
    
    stage("Test"){
        withCredentials([sshUserPrivateKey(credentialsId: 'jenkins-key', keyFileVariable: 'SSH_KEY', usernameVariable: 'SSH_USERNAME')]) {
            sh "ansible-playbook --private-key=$SSH_KEY -i '$params.IP,' -u $SSH_USERNAME main.yml"
        }
    }
}
