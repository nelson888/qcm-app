
export function nDaysAfter(n: number) {
    let d: Date = new Date();
    d.setTime(d.getTime() + (n*24*60*60*1000));
    return d;
}

export function deleteCookie(cname: string) {
    document.cookie = `${cname}=; expires=Thu, 18 Dec 2013 12:00:00 UTC; path=/`;
}

export function setCookie(cname: string, cvalue: string, d:Date) {
    var expires = "expires="+d.toUTCString();
    document.cookie = cname + "=" + cvalue + ";" + expires + ";path=/";
}

export function getCookie(cname: string) {
    let name: string = cname + "=";
    let ca: string[] = document.cookie.split(';');
    for(let i = 0; i < ca.length; i++) {
        let c = ca[i];
        while (c.charAt(0) === ' ') {
            c = c.substring(1);
        }
        if (c.indexOf(name) === 0) {
            return c.substring(name.length, c.length);
        }
    }
    return "";
}
