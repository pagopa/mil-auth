import http from 'k6/http';
import { sleep, check } from 'k6';

export const options = {
	maxRedirects: 4,
	duration: '10s',
	vus: 100,
	thresholds: {
		// 90% of requests must finish within 4s, 95% within 5s.
		'http_req_duration': ['p(90) < 2500', 'p(95) < 3000'],
		// During the whole test execution, the error rate (HTTP status 500) must be lower than 1%.
		'checks{myTag:serverError}': ['rate < 0.01'],
	}
};

export default function() {
	// client_credentials - VAS Layer - ok
	check(
		http.post(
			"https://mil-d-apim.azure-api.net/mil-auth/token",
			{
				client_secret: "5ceef788-4115-43a7-a704-b1bcc9a47c86",
				client_id: "3965df56-ca9a-49e5-97e8-061433d4a25b",
				grant_type: "client_credentials"
			},
			{
				params: {
					headers: {
						'Content-Type': 'application/x-www-form-urlencoded',
						'RequestId': "00000000-0000-0000-0000-100000000001",
						'AcquirerId': "4585625",
						'Channel': "POS",
						'MerchantId': "28405fHfk73x88D",
						'TerminalId': "01234567"
					}
				}
			}
		),
		{
			'status is 500': (r) => r.status == 500
		},
		{
			myTag: 'serverError'
		}
	);

	// JWKS
	for (let i = 0; i < 6; i++) {
		sleep(1);
		
		check(
			http.get("https://mil-d-apim.azure-api.net/mil-auth/.well-known/jwks.json"),
			{
				'status is 500': (r) => r.status == 500
			},
			{
				myTag: 'serverError'
			}
		);
	}
	
	sleep(5);
}
