import * as React from "react"
import PropTypes from "prop-types"
import axios from "axios"
import {
	StyleSheet,
	Text,
	View,
	ViewPropTypes,
	NativeModules,
	Alert,
	DeviceEventEmitter,
	requireNativeComponent,
	Dimensions,
	TouchableOpacity,
	ScrollView
} from "react-native"

const NativeHelloWorld = NativeModules.HelloWorld
const LytCalendarView = requireNativeComponent(
	"LytCalendarView",
	{
		name: "LytCalendarView",
		propTypes: {
			cities: PropTypes.array,
			// selected: PropTypes.array,
			...ViewPropTypes
		}
	},
	{ nativeOnly: { onChange: true } }
)

export default class App extends React.Component<{}> {
	state = {
		areas: [],
		loaded: false
		// selected: []
	}

	componentDidMount() {
		return axios
			.get("http://ymdev.baixing.com.cn/mili/api/areas")
			.then(({ data }) => {
				const areas = []
				for (const section of data) {
					for (const city of section.children) {
						areas.push({
							name: city.name,
							pinyin: city.pinyin,
							selected: false
						})
					}
				}
				this.setState({ areas, loaded: true })
			})
		// DeviceEventEmitter.addListener("lytNativeEvent", e => {
		// 	console.log("Event greeting has been sent to ")
		// 	console.log(e)
		// })
		// Alert.alert(NativeHelloWorld.MODULE_OWNER_NAME, "", [
		// 	{
		// 		text: "OK",
		// 		onPress: () => {
		// 			// NativeHelloWorld.greeting('callback', (name: string) => {
		// 			//   console.log('Greeting has been sent to ' + name)
		// 			// })
		// 			// NativeHelloWorld.asyncGreeting('async').then((name: string) => {
		// 			//   console.log('Async greeting has been sent to ' + name)
		// 			// })
		// 			NativeHelloWorld.eventGreeting("event")
		// 		}
		// 	}
		// ])
	}

	onSelect = event => {
		const { city: selected } = event.nativeEvent

		const newAreas = []

		for (const city of this.state.areas) {
			newAreas.push({
				name: city.name,
				pinyin: city.pinyin,
				selected:
					selected === city.name ? !city.selected : city.selected
			})
		}

		// const { selected } = this.state
		// const set = new Set<string>(selected)
		// if (set.has(city)) {
		// 	set.delete(city)
		// } else {
		// 	set.add(city)
		// }
		// this.setState({ selected: Array.from(set) })
		// console.log(Array.from(set))

		// console.log(event)
		// console.log(event.nativeEvent.city)

		this.setState({ areas: newAreas })
	}

	render() {
		const { width, height } = Dimensions.get("window")
		const { loaded, areas } = this.state
		return (
			<View>
				<ScrollView
					style={{ width: "100%", height: 50 }}
					horizontal={true}
				>
					{areas.filter(item => item.selected).map(item => (
						<Text style={{ margin: 10 }} key={item.name}>
							{item.name}
						</Text>
					))}
				</ScrollView>
				{/* <TouchableOpacity onPress={() => this.onSelect("阿拉善")}>
					<Text>改selected</Text>
				</TouchableOpacity> */}
				{!loaded ? (
					<Text>Loading...</Text>
				) : (
					<LytCalendarView
						height={height}
						width={width}
						cities={areas}
						onChange={this.onSelect}
						// selected={selected}
						// onSelect={this.onSelect}
					/>
				)}
			</View>
		)
	}
}

const styles = StyleSheet.create({
	container: {
		flex: 1,
		backgroundColor: "#fff",
		alignItems: "center",
		justifyContent: "center"
	}
})
