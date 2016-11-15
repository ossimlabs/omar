class Foobar
{
  constructor(name='World')
  {
        this.name = name;
  }

  sayHello() {
    alert(`Hello ${this.name}!`);

//    const name = 'scottie';

//    name = 'aaron';
  }
}

var foobar = new Foobar();

foobar.sayHello();
