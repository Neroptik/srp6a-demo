if (localStorage.getItem('pgppriv') != null && localStorage.getItem('pgppub') != null) {
    console.log("Already in localstorage");
    console.log("Private Key:\n" + localStorage.getItem('pgppriv'));
    console.log("Public Key:\n" + localStorage.getItem('pgppub'));
    console.log("Passphrase: " + localStorage.getItem('passphrase'));
} else {
    console.log("Retrieve from server");
    $.get("/pgp", function(data) {
        localStorage.setItem('pgppriv', data.pgppriv);
        localStorage.setItem('pgppub', data.pgppub);
        console.log("Private Key:\n" + localStorage.getItem('pgppriv'));
        console.log("Public Key:\n" + localStorage.getItem('pgppub'));
        console.log("Passphrase: " + localStorage.getItem('passphrase'));
    });
}
