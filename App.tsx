import React, { useEffect, useState } from 'react';
import {
  Button,
  NativeAppEventEmitter,
  NativeEventEmitter,
  PermissionsAndroid,
  Text,
  View,
  Platform
} from 'react-native';
import { NativeModules } from 'react-native';
import CustomTextView from './custom-components/CustomTextView';
import CustomCameraView from './custom-components/CustomCameraPreview';
const {RunServiceModule} = NativeModules

const eventEmitter = new NativeEventEmitter(RunServiceModule)

function App(): React.JSX.Element {
  PermissionsAndroid.request(PermissionsAndroid.PERMISSIONS.POST_NOTIFICATIONS).then((permissionStatus) => {
    console.log(permissionStatus);
  })
  const [isPermissionAccepted, setIsPermissionAccepted] = useState(false)
  const [isCameraRunning, setIsCameraRunning] = useState(false)

  const callbackToRun = () => {
    console.log("This is our trigged callback");
  }

  eventEmitter.addListener("trigger", (event) => {
    callbackToRun()
  })
  const startServiceCallback = () => {
    RunServiceModule.startService().then(((value: string) => {
      console.log(value)
    }))
  }

  const stopServiceCallback = () => {
    RunServiceModule.stopService().then(((value: string) => {
      console.log(value)
    }))
  }


async function requestCameraPermission() {
  if (Platform.OS === 'android' && Platform.Version >= 23) {
    try {
      const granted = await PermissionsAndroid.request(
        PermissionsAndroid.PERMISSIONS.CAMERA,);
      if (granted === PermissionsAndroid.RESULTS.GRANTED) {
        setIsPermissionAccepted(true)
      } else {
        console.log('Camera permission denied');
      }
    } catch (err) {
      console.warn(err);
    }
  }
}
  useEffect(() => {
    requestCameraPermission() 
  }, [])

  useEffect(() => {
    if(isCameraRunning){
      startServiceCallback()
    }else {
      stopServiceCallback()
    }
  }, [isCameraRunning])

  return (
    <View style={{gap: 8}}>
      <CustomCameraView run={isCameraRunning} style={{width: '100%', height: 600}}/>
      <Button title='Start' onPress={() => setIsCameraRunning((value) => true)}/>
      <Button title='Stop' onPress={() => setIsCameraRunning((value) => false)}/>
    </View>
  );
}

export default App;
