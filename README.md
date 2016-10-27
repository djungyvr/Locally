# Locally

## Setup

### AWS Configuration
Obtain AwsConfiguration.java from the developers and place it inside the AWS Package, this contains the necessary information for using AWS. More specifically Cognito and DynamoDB

### API Keys
Obtain api_keys.xml from the developers and place it inside the res/values/ directory


### Gradle Build
#####Ensure your "/Locally/app/build.gradle" file is the same as below. 
#####** NOTE THIS IS NOT THE SAME AS "/Locally/build.gradle" DO NOT CHANGE THAT FILE**

/Locally/app/build.gradle (Last Updated 27/10/16)
```
apply plugin: 'com.android.application'

android {
    compileSdkVersion 23
    buildToolsVersion "23.0.1"

    defaultConfig {
        applicationId "com.example.djung.locally"
        minSdkVersion 16
        targetSdkVersion 23
        versionCode 1
        versionName "1.0"
    }
    buildTypes {
        release {
            minifyEnabled false
            proguardFiles getDefaultProguardFile('proguard-android.txt'), 'proguard-rules.pro'
        }
    }
}

dependencies {
    compile fileTree(dir: 'libs', include: ['*.jar'])
    testCompile 'junit:junit:4.12'

    compile 'com.android.support:appcompat-v7:23.4.0'
    compile 'com.amazonaws:aws-android-sdk-core:2.2.+'
    compile 'com.amazonaws:aws-android-sdk-s3:2.2.+'
    compile 'com.amazonaws:aws-android-sdk-ddb:2.2.+'
    compile 'com.amazonaws:aws-android-sdk-ddb-mapper:2.2.+'
    compile 'com.android.support:design:23.4.0'
    compile 'com.android.support:cardview-v7:23.4.0'
    compile 'com.google.android.gms:play-services:9.6.1'
}
```
