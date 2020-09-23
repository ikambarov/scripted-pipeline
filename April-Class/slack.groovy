node {
  stage("Building"){
    echo 'Building..'
  }
  stage("Testing"){
    echo 'Testing..'
  }
  stage("Deploying "){
    echo 'Deploying....'
  }
  stage("Send Notification to Slack"){
    slackSend color: '#BADA55', message: 'Finished deployment!'
  }
}
