properties([
    parameters([
        string(defaultValue: '', description: 'Enter IP Address', name: 'IP', trim: true)
    ])
])

node {
    stage("Pull repo"){
        git 'https://github.com/ikambarov/ansible-melodi.git'
    }
    
    stage('Install Melodi'){
        ansiblePlaybook become: true, colorized: true, credentialsId: 'jenkins-master', disableHostKeyChecking: true, inventory: "${ params.IP },", playbook: 'main.yml'
    }
}