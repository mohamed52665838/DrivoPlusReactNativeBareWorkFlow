import { requireNativeComponent, ViewStyle } from "react-native";

const CustomTextView = requireNativeComponent<{text: string, style: ViewStyle}>("CustomTextView")

export default CustomTextView