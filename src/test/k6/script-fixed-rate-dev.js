import http from 'k6/http';

export const options = {
    discardResponseBodies: true,
    scenarios: {
        contacts: {
            executor: 'constant-arrival-rate',
            duration: '5m',
            rate: __ENV.RATE,
            timeUnit: '1s',
            preAllocatedVUs: 1000
        }
    },
    thresholds: {
        'http_req_duration{status:200}': ['max>=0'],
        'http_req_duration{status:401}': ['max>=0'],
        'http_req_duration{status:429}': ['max>=0'],
        'http_req_duration{status:500}': ['max>=0'],
        'http_req_duration{status:502}': ['max>=0'],
        'http_req_duration{method:POST}': ['max>=0']
    }
};

export default function() {
    http.post(
        __ENV.URL,
        {
            client_secret: __ENV.CLIENT_SECRET,
            client_id: __ENV.CLIENT_ID,
            grant_type: "client_credentials"
        },
        {

            headers: {
                'Content-Type': 'application/x-www-form-urlencoded',
                'RequestId': "00000000-0000-0000-0000-999999999999"
            }
        }
    );
}
