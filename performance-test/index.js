import http from 'k6/http';
import { sleep, check } from 'k6';

export default function () {
//    const userIds = Array(100).fill(0).map((value, i) => i.toString().padStart(2, '0'));
//    const responses = userIds.forEach(userId => {
//        const res = http.get(`http://localhost:8080/users/${userId}`)
//        check(res, {'is successful': r => r.status === 200 })
//    });
    const userId = Math.floor(Math.random() * 10000).toString().padStart(4, '0');
    const res = http.get(`http://localhost:8080/users/${userId}`)
    check(res, {'is successful': r => r.status === 200 })
}