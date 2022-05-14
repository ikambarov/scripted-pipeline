properties([
    parameters([
        string(description: 'Enter Linux IP Address', name: 'IP', trim: true)
        ])
    ])

node {
    stage("Pull Playbook"){
        git 'https://github.com/ikambarov/ansible-Flaskex.git'
    }

    stage("Pull repo"){
        withEnv(['FLASKEX_REPO=https://github.com/anfederico/flaskex.git', 'FLASKEX_BRANCH=master']) {
            ansiblePlaybook credentialsId: 'jenkins-key', inventory: "$params.IP,", playbook: 'pull_repo.yml'
        }
    }

    stage("Install Python"){
        ansiblePlaybook credentialsId: 'jenkins-key', inventory: "$params.IP,", playbook: 'install_python.yml'
    }

    stage("Install Prerequisites"){
        ansiblePlaybook credentialsId: 'jenkins-key', inventory: "$params.IP,", playbook: 'prerequisites.yml'
    }
    
    stage("Start App"){
        ansiblePlaybook credentialsId: 'jenkins-key', inventory: "$params.IP,", playbook: 'start_app.yml'
    }
}
