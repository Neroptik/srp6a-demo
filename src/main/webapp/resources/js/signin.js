var Login = {
  srpClient: null,
  password: null,
  email: null, 

  options: {
    emailId: '#email-login',
    formId: '#login-form',
    registerBtnId: '#loginBtn',
    passwordId: '#password-login',
    passwordSaltId: '#password-login-salt',
    passwordVerifierId: '#password-login-verifier'
  },

  defaults: {
    challengeResponse: {},
    verifyResponse: {}
  },

  initialize: function (options) {
    var me = this;

    if (options) {
      me.options = options;
    }

    $(me.options.formId).on('submit', function (e) {
      e.preventDefault();
      
      var data = {
        email: me.getEmail()
      };

      $('#login-output').append('<b>-> Client, I</b><br/>' + data.email + '<br/>');

      $.post(me.options.challengeUrl, data, function () {
        me.onChallengeResponse.apply(me, arguments);
      }, 'json');

      return false;
    });
  },

  onChallengeResponse: function (response) {
    var me = this;

    $('#login-output').append('<b><- Server, Salt</b><br/>' + response.salt + '<br/>');
    $('#login-output').append('<b><- Server, B</b><br/>' + response.b + '<br/>');

    var client = me.getClient();
    
    var start = Date.now();
    
    try {
    	client.step1(me.email, me.password);
    } catch(e) {
    	alert("Client session is in end state and cannot be reused so refreshing the demo page to start again.");
    	window.location = window.location;
    }
    
    var credentials = client.step2(response.salt, response.b);

    var end = Date.now();

    var data = {
    		j_username: me.getEmail(),
    		j_password: credentials.M1+":"+credentials.A
    };
    
	console.log("j_username: "+ data.j_username);
	console.log("j_password: "+ data.j_password);

    $('#login-output').append('<b>-> Client, M</b><br/>' + data.M1 + ' crypto took ' + (end-start) + 'ms <br/>');

    $.post(me.options.securityCheckUrl, data, function () {
      me.onRespondResponse.apply(me, arguments);
    }, 'json');
  },

  onRespondResponse: function (response) {
    var me = this;

    if (response.error) {
      $('#login-output').append('<b><- Server</b><br/>' + response.error + '<br/>');
    } else {
      $('#login-output').append('<b><- Server, M2</b><br/>' + response.M2 + '<br/>');
      if (me.getClient().step3(response.M2)) {
        $(document).trigger('success');
        $('#login-output').append('<b>Success!</b>');
      } else {
        $('#login-output').append('<b>Failure!</b>');
      }
    }

    $('#login-output').append('<hr/>');
  },

  getEmail: function () {
    return $(this.options.emailId).attr('value');
  },

  getPassword: function () {
    return $(this.options.passwordId).attr('value');
  },

  getClient: function () {
  
    if (this.srpClient === null || this.getPassword() !== this.password || this.getEmail() !== this.email) {
      this.password = this.getPassword();
      this.email = this.getEmail();
  	  var jsClientSession = new SRP6JavascriptClientSessionSHA256();
      this.srpClient = jsClientSession;
    }

    return this.srpClient;
  }
}