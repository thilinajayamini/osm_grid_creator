
var db;
function init() {
    window.indexedDB = window.indexedDB || window.mozIndexedDB || window.webkitIndexedDB || window.msIndexedDB;

//prefixes of window.IDB objects
    window.IDBTransaction = window.IDBTransaction || window.webkitIDBTransaction || window.msIDBTransaction;
    window.IDBKeyRange = window.IDBKeyRange || window.webkitIDBKeyRange || window.msIDBKeyRange;

    if (!window.indexedDB) {
        window.alert("Your browser doesn't support a stable version of IndexedDB.");
    }

    var db_name= document.getElementById('uid').innerHTML;

    var request = window.indexedDB.open(db_name, 1);

    request.onerror = function (event) {
        console.log("error: ");
    };

    request.onsuccess = function (event) {
        db = request.result;
        console.log("success: " + db);
        var objectStore = db.transaction(["1200"]).objectStore("1200");
        objectStore.openCursor().onsuccess = function (event) {
            var cursor = event.target.result;

            if (cursor) {

                console.log(cursor.key + " - " + cursor.value.name);
                markaAsDone(cursor.key);
                cursor.continue();
            }

            else {
                console.log("Done!");
            }
        };
    };


    request.onupgradeneeded = function (event) {
        var db = event.target.result;
        db.createObjectStore("1200", {keyPath: "id"});
        console.log("success: created");

    };
}

function markaAsDone(id) {

    var x = document.getElementById(id);
    x.parentElement.style.backgroundColor = 'lightgreen';
    x.parentElement.style.opacity = '0.6';

}

function markaAsUnDone(id) {

    var x = document.getElementById(id);
    x.parentElement.style.backgroundColor = 'transparent';
    x.parentElement.style.opacity = '1';

}

function veryfy(item) {
    if (confirm("Mark this area as done ?")) {
        markaAsDone(item.id);
        add(item.id);
    } else {
        var request = db.transaction(["1200"], "readwrite")
                .objectStore("1200").get(item.id);

        request.onerror = function (event) {
            console.log("Unable to delete data from database!");
        };

        request.onsuccess = function (event) {
            if (request.result) {
                remove(item.id);
                markaAsUnDone(item.id);
                console.log(item.id + "removed from your database!");
            }

            else {
                console.log("nothing to remove from your database!");
            }
        };
    }
}

function loadAll() {
    var objectStore = db.transaction(["1200"]).objectStore("1200");

    objectStore.openCursor().onsuccess = function (event) {
        var cursor = event.target.result;

        if (cursor) {

            console.log(cursor.key + " - " + cursor.value.name);
            markaAsDone(cursor.key);
            cursor.continue();
        }

        else {
            console.log("Done!");
        }
    };
}

function add(grid_id) {
    var request = db.transaction(["1200"], "readwrite")
            .objectStore("1200")
            .add({id: grid_id, name: "Done"});

    request.onsuccess = function (event) {
        console.log(grid_id + "has been added to your database.");
    };

    request.onerror = function (event) {
        alert("Unable to add data\r\n " + grid_id + " into your database! ");
    };
}

function remove(grid_id) {
    var request = db.transaction(["1200"], "readwrite")
            .objectStore("1200")
            .delete(grid_id);

    request.onsuccess = function (event) {
        console.log(grid_id + "entry has been removed from your database.");
    };
}