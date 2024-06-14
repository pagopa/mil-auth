import http from 'k6/http';
import { sleep, check } from 'k6';

export const options = {
	discardResponseBodies: true,
	scenarios: {
		contacts: {
			executor: 'constant-arrival-rate',
			duration: '5m',
			rate: 25,
			timeUnit: '1s',
			preAllocatedVUs: 200,
		},
	},
};

export default function() {
	http.post(
		"https://mil-d-auth-ca.agreeablestone-406ca858.westeurope.azurecontainerapps.io/token",
		{
			client_secret: "5ceef788-4115-43a7-a704-b1bcc9a47c86",
			client_id: "3965df56-ca9a-49e5-97e8-061433d4a25b",
			grant_type: "client_credentials"
		},
		{

			headers: {
				'Content-Type': 'application/x-www-form-urlencoded',
				'RequestId': "00000000-0000-0000-0000-100000000001",
				'AcquirerId': "4585625",
				'Channel': "POS",
				'MerchantId': "28405fHfk73x88D",
				'TerminalId': "01234567"
			}
		}
	);
}
