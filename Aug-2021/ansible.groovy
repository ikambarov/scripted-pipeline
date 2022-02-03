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
        ansiblePlaybook credentialsId: 'jenkins-key', disableHostKeyChecking: true, inventory: "${ params.ip },", playbook: 'main.yml'
    }
}
