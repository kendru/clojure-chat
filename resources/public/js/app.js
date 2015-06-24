(function (window) {
  var url = 'ws://' +
            window.location.host +
            window.location.pathname +
            cfg.url.substring(1),
      ws = new WebSocket(url),
      $messages = $('#messages'),
      $name = $('#user-name'),
      $message = $('#message'),
      $submit = $('#submit'),
      msgTemplate = _.template($('#tpl-message').html()),
      submitMessage;

  submitMessage = function () {
    var name = $name.val(),
        message = $message.val();

    if (message === '') {
      return;
    }

    ws.send(JSON.stringify({user: name, msg: message}));
    $message.val('');
  };

  $submit.on('click', submitMessage);

  $message.on('keyup', function (e) {
    if (e.keyCode === 13) {
      submitMessage();
    }
  });

  ws.onmessage = function (msg) {
    var data = JSON.parse(msg.data),
        date = new Date(data.time);
    data.user = data.user || null;
    data.time = (date.getHours() - (date.getHours() > 12 ? 12 : 0)) +
      ':' + date.getMinutes();
    $messages.append(msgTemplate(data));
  };

  window.onbeforeunload = function() {
    ws.close()
  };
}(window));
