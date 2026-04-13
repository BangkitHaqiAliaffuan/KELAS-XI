// factory

function createCircle(radius) {
  return {
    radius,
    draw: () => {
      console.log(radius, "draw");
    },
  };
}

const anotherCircle = createCircle(2);
anotherCircle.draw();

// constructor
function circle(radius) {
  this.radius = radius;
  this.draw = function () {
    console.log("draw", this.radius);
  };
}

const buildCircle = new circle(1);

buildCircle.draw();

// use case factory function

function createBook(title, author, price) {
  return {
    title,
    author,
    price,
    getInfo: () => {
        console.log('data buku berhasil diambil', title, author, price)
    },
    applyDiscount:(percent)=>{
        const total = ((100 - percent)/ 100) * price 
        console.log('berikut ini adalah data diskon', total)
    },
  };
}


const LaskarPelangi = createBook('Laskar Pelangi', 'bang Surya', 2000)


const Femboy = createBook('Femboy Attractiveness', 'Bangkit Haqi Aliaffuan', 50000)

// Femboy.getInfo()
// Femboy.applyDiscount(30)


function User(username, email){
    this.username = username,    
    this.email = email,
    this.isOnline = false,
    this.login = function(){
        this.isOnline = true
        console.log(`User ${this.username}, telah login dengan sukses. Status Online ${this.isOnline}`)
    }    
    this.logout = function(){
        this.isOnline = false
        console.log(`User ${this.username}, telah logout dengan sukses. Status Online: ${this.isOnline}`)
    }    
}


const Haqi = new User('Haqi', 'haqi@gmail.com');
const Ale = new User('Ale', 'ale@gmail.com');

Haqi.logout()




