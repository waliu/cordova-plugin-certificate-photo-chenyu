var exec = require('cordova/exec');
var CertificatePhoto = {
    startCamera(args, success, error) {
        exec(success, error, 'CertificatePhoto', 'startCamera', [args]);
    }
}
module.exports = CertificatePhoto;