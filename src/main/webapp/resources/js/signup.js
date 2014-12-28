var Register = {
	verifier : null,
	password : null,
	srpClient : null,
	email : null,

	options : {
		emailId : '#email-login',
		formId : '#register-form',
		registerBtnId : '#registerBtn',
		passwordId : '#password',
		passwordSaltId : '#password-salt',
		passwordVerifierId : '#password-verifier'
	},

	initialize : function(options) {
		var me = this;

		if (options) {
			me.options = options;
		}

		$(options.formId).on('submit', $.proxy(function() {
			me.onPasswordChange();
		}, me));

		$(options.emailId).on('keyup', $.proxy(function(event) {
			random16byteHex.advance(Math.floor(event.keyCode / 4));
			me.onPasswordChange();
		}, me));

		$(options.passwordId).on(
				'keyup',
				$.proxy(function(event) {
					$(event.currentTarget).val().length ? me.enableSubmitBtn()
							: me.disableSubmitBtn();
					random16byteHex.advance(Math.floor(event.keyCode / 4));
					me.onPasswordChange();
				}, me));

	},

	disableSubmitBtn : function() {
		$(this.options.registerBtnId).attr('disabled', true);
	},

	enableSubmitBtn : function() {
		$(this.options.registerBtnId).removeAttr('disabled');
	},

	onPasswordChange : function() {
		var me = this;

		var verifier = this.generateVerifier();

		if (typeof verifier !== 'undefined' && verifier !== null
				&& verifier != "") {
			$(me.options.passwordSaltId).attr('value', verifier.salt);
			$(me.options.passwordVerifierId).attr('value', verifier.verifier);

			$('#password-salt-output').text(verifier.salt);
			$('#password-verifier-output').text(verifier.verifier);
		}
	},

	getEmail : function() {
		//var email = $(this.options.emailId).val();
		//console.log("email:" + email);
		return $(this.options.emailId).val();
	},

	getPassword : function() {
		//var pp = $(this.options.passwordId).val();
		//console.log("password:" + pp);
		return $(this.options.passwordId).val();
	},

	getClient : function() {

		if (this.srpClient === null) {
			var jsClientSession = new SRP6JavascriptClientSessionSHA256();
			this.srpClient = jsClientSession;
		}

		return this.srpClient;
	},

	generateVerifier : function() {
		this.email = this.getEmail();
		this.password = this.getPassword();
		if (this.email !== null && this.email != ""
				&& typeof this.password !== 'undefined'
				&& this.password !== null && this.password != "") {
			var client = this.getClient();
			/**
			Consider passing a secure random on a hidden field and passing that as the optional argument
			e.g. 
			var salt = client.generateRandomSalt(serverSecureRandomValue);
			 */
			var salt = client.generateRandomSalt();
			var v = client.generateVerifier(salt, this.email, this.password);
			this.verifier = {
				'salt' : salt,
				'verifier' : v
			}
		}

		return this.verifier;
	}
}