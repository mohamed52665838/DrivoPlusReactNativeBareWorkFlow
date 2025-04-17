import { requireNativeComponent } from 'react-native';
import { ViewStyle } from 'react-native';

const CustomCameraView = requireNativeComponent<{ style: ViewStyle, run: Boolean }>('CustomCameraView');

export default CustomCameraView
