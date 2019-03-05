var Pad = {
    save: function() {
        var text = this.getText();
        var csrf = this.getCsrf();
        var pgppriv = localStorage.getItem('pgppriv');
        var pgppub = localStorage.getItem('pgppub');
        var passphrase = localStorage.getItem('passphrase');
        console.log(text);
        console.log("Private Key:\n" + pgppriv);
        console.log("Public Key:\n" + pgppub);
        console.log("Passphrase: " + passphrase);
        const encryptText = async() => {
            const privKeyObj = (await openpgp.key.readArmored(pgppriv)).keys[0]
            await privKeyObj.decrypt(passphrase)
            const options = {
                message: openpgp.message.fromText(text),
                publicKeys: (await openpgp.key.readArmored(pgppub)).keys,
                privateKeys: [privKeyObj]
            }
            openpgp.encrypt(options).then(ciphertext => {
                encrypted = ciphertext.data;
                console.log(encrypted);
                $.ajax({
                    type: "POST",
                    url: "/setpad",
                    data: "text=" + encodeURIComponent(encrypted) + "&_csrf=" + csrf,
                    success: function(data) {
                        console.log(data)
                    }
                });
                return false;
            })
        }
        encryptText()
    },
    load: function() {
        $.get("/getpad", function(data) {
            if (data.length > 0) {
                console.log(data);
                var pgppriv = localStorage.getItem('pgppriv');
                var pgppub = localStorage.getItem('pgppub');
                var passphrase = localStorage.getItem('passphrase');
                const decryptText = async() => {
                    const privKeyObj = (await openpgp.key.readArmored(pgppriv)).keys[0]
                    await privKeyObj.decrypt(passphrase)
                    const options = {
                        message: await openpgp.message.readArmored(data),
                        publicKeys: (await openpgp.key.readArmored(pgppub)).keys,
                        privateKeys: [privKeyObj]
                    }
                    openpgp.decrypt(options).then(plaintext => {
                        $("#text").val(plaintext.data);
                    })
                }
                decryptText()

            }
        });
    },
    getText: function() {
        return $("#text").val();
    },
    getCsrf: function() {
        return $("#csrf").val();
    }
}
