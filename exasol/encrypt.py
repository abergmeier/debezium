
import base64
import rsa
import sys

pemKey = sys.argv[1]
value = sys.argv[2]

if sys.version_info.major >= 3:
    pk = rsa.PublicKey.load_pkcs1(bytes(pemKey, 'utf-8'))
    encrypted = base64.b64encode(
        rsa.encrypt(value.encode('utf-8'), pk)).decode('utf-8')
else:
    pk = rsa.PublicKey.load_pkcs1(pemKey)
    encrypted = base64.b64encode(
        rsa.encrypt(value.encode('utf-8'), pk))

print(encrypted)
