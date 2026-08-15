import { List } from "react-window";

function Example({ names }) {
    return (
        <List
            rowComponent={RowComponent}
            rowCount={names.length}
            rowHeight={100}
            rowProps={{ names }}
            style={{ height: 400, width: 400 }}
        />
    );
}

function RowComponent({ index, names, style }) {
    return (
        <div
            className="flex items-center justify-between px-2"
            style={style}
        >
            <span>{names[index]}</span>

            <div className="text-slate-500 text-xs">
                {`${index + 1} of ${names.length}`}
            </div>
        </div>
    );
}

const namesList = [
    "Gopi",
    "Rahul",
    "Anil",
    "Suresh",
    "Kiran",
    "Vijay",
    "Arjun",
    "Ravi",
    "Prakash",
    "Manoj",
    "Sai",
    "Naveen",
    "Ajay",
    "Ramesh",
    "Mahesh",
    "Karthik",
    "Varun",
    "Surya",
    "Tarun",
    "Rohit",
];

function Demo() {
    const names = Array.from({length: 1000000}, () => namesList[Math.floor(Math.random() * namesList.length)])

    return (
        <div className="p-4">
            <h2 className="mb-4 text-lg font-semibold">
                Names
            </h2>

            <Example names={names} />
        </div>
    );
}

export default Demo;