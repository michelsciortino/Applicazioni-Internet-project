export class Child {
    name: string;
    surname: string;
    cf: string;
    parentId: string;

    constructor(name?: string, surname?: string, cf?: string, parentId?: string) {
        this.name = name;
        this.surname = surname;
        this.cf = cf;
        this.parentId = parentId;
    }
}
