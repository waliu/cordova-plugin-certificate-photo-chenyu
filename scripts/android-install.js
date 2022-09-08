var fs = require("fs");
var path = require('path');

// let temp = {
//     install(context) {
//         //初始化名称
//         let tempPackName = "com.hosp123.testapp.location";
//         //获取ConfigParser 函数
//         let ConfigParser = ConfigParser = context.requireCordovaModule('cordova-common').ConfigParser;
//         // 获取config 文件
//         let config = new ConfigParser(path.join(context.opts.projectRoot, "config.xml"));
//         // 获取android 包名
//         let packageName = config.android_packageName() || config.packageName();
//
//         console.log(packageName)
//     }
// }
// temp.install(fs);

module.exports = function (context) {
    install.main(context).then();
}
let install = {
    async main(context) {
        let tempPackName = "com.certificate.photo";
        let tempTargetDir = "/certificate/photo";
        //获取ConfigParser 函数
        let ConfigParser = context.requireCordovaModule('cordova-common').ConfigParser;
        // 获取config 文件
        let config = new ConfigParser(path.join(context.opts.projectRoot, "config.xml"));
        // 获取android 包名
        let packageName = config.android_packageName() || config.packageName();
        // 插件 class 路径
        let classNames = await this.readdir(path.join(__dirname, "../src/android/java"));
        //遍历所有类
        for (let i = 0; i < classNames.length; i++) {
            //需要更改的类的地址
            let fullFileUrl = path.join(context.opts.plugin.dir, 'src/android/java/', classNames[i]);
            // 需要更改的类的 具体数据
            let fullFileData = fs.readFileSync(fullFileUrl, 'utf-8');
            // 输出路径
            let targetDir = path.join(context.opts.projectRoot, "platforms/android/app/src/main/java", tempTargetDir);
            // 替换包名
            let replaceFullFileData = fullFileData.replace(tempPackName, packageName);
            //保存
            fs.writeFile(path.join(targetDir, classNames[i]), replaceFullFileData, function (err) {
                if (err) return err;
            });

        }
        // console.log(classNames);
    }, async readdir(path) {
        return new Promise((resolve, reject) => {
            fs.readdir(path, (err, files) => {
                resolve(files)
            })
        });
    }
}