Feature: As Server Application, I want to authenticate myself, so that I can get and access token


  Rule: There is not a fiscal code to protect


    Scenario: Access token without refresh token is requested with correct credentials and roles exist
      Given correct client credentials
        And client has roles
        And no fiscal code
       When access token is requested
        And refresh token is not requested
       Then get access token
        And access token does not have fiscal code claim
        And do not get refresh token

    
    Scenario: Access token with refresh token is requested with correct credentials and roles exist
      Given correct client credentials
       And client has roles
       And no fiscal code
      When access token is requested
       And refresh token is requested
      Then get access token
        And access token does not have fiscal code claim
      And get refresh token


    Scenario: Access token without refresh token is requested with correct credentials and roles do not exist
    
    
    Scenario: Access token without refresh token is requested with wrong credentials do not exit

    
    Scenario: Access token with refresh token is requested with wrong credentials

    
  Rule: There is a fiscal code to protect

  
    Scenario: Access token without refresh token is requested with correct credentials

    
    Scenario: Access token with refresh token is requested with correct credentials


    Scenario: Access token without refresh token is requested with wrong credentials

    
    Scenario: Access token with refresh token is requested with wrong credentials