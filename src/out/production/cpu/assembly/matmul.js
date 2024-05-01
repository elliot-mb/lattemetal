const A = [-1, 6, 6, 1];
const B = [2, 3, 4, 5];
const C = [null, null, null, null];
let m = 2;
let p = 2;
let n = 2;
let j = 0;
for(j; j < m; j++){
    let k = 0;
    for(k; k < n; k++){
        let s2 = 0;
        let i = 0;
        for(i; i < p; i++){
            s2 += A[(m * j) + i] * B[(i * p) + k];
        }
        C[(j * m) + k] = s2;
    }
}
console.log(C);